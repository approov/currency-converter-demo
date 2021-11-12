# System packages
import logging
import requests
import time
import json
import hashlib

from babel.numbers import format_currency
from random import choice
from os import getenv

# Third part packages
from ring import lru
from dotenv import load_dotenv, find_dotenv
from flask import Flask, request, abort, make_response, jsonify

api = Flask(__name__)

logging.basicConfig(level=logging.DEBUG)
log = logging.getLogger(__name__)

load_dotenv(find_dotenv(), override=True)

import api_key_decorator

HTTP_PORT = int(getenv('HTTP_PORT', 5000))
API_URL = getenv('API_URL')
API_KEY = getenv('API_KEY')
REPORT_MAX_LENGTH = getenv('REPORT_MAX_LENGTH', 12000)

def _getHeader(key, default_value = None):
    return request.headers.get(key, default_value)

def _isEmpty(token):
    return token is None or token == ""

@api.route("/")
def endpoints():
    return jsonify(
        currency_convert="/currency/convert/1/from/GBP/to/EUR",
    )

# /currency/convert/1.0/from/GBP/to/EUR
@api.route('/currency/convert/<string:value_to_convert>/from/<string:from_currency>/to/<string:to_currency>', methods=['GET'])
@api_key_decorator.check_api_key
def currency_convert(value_to_convert, from_currency, to_currency):
   return _calculate_conversion(value_to_convert, from_currency, to_currency)

# /v2/currency/convert/1.0/from/GBP/to/EUR
@api.route('/v2/currency/convert/<string:value_to_convert>/from/<string:from_currency>/to/<string:to_currency>', methods=['GET'])
@api_key_decorator.check_api_key
def currency_convert_v2(value_to_convert, from_currency, to_currency):
    return _calculate_conversion(value_to_convert, from_currency, to_currency)

def _calculate_conversion(value_to_convert, from_currency, to_currency):

    currency_query = from_currency + "_" + to_currency

    response = _get_conversion_rate(currency_query)

    try:
        json_response = response.json()
    except json.decoder.JSONDecodeError as e:
        log.error("Invalid JSON in the response | %s", e)
        _get_conversion_rate.delete(currency_query)
        abort(make_response(jsonify(error="Currency Rates Server timeout. Please try again later."), 400))
        #return jsonify(error="Currency Rates Server timeout. Please try again later.")

    if currency_query in json_response:
        conversion_rate = json_response[currency_query]
        log.info(" CONVERSION RATE: %f", conversion_rate)

        converted_value = conversion_rate * float(value_to_convert)
        log.info(" CONVERTED VALUE: %f", converted_value)

        converted_value = format_currency(converted_value, currency = to_currency)

        return jsonify(converted_value=converted_value)

    if not json_response:
        log.error("CURRENCY CONVERTER FREE API ERROR: Check if the currency codes are valid.")
        abort(make_response(jsonify(error="Check if the currency codes are valid."), 400))

    if "error" in json_response:
        log.error("CURRENCY CONVERTER FREE API ERROR: " + json_response["error"])
        abort(make_response(jsonify(error="Reached free API limit. Please retry later."), 400))

@lru(expire=3600)
def _get_conversion_rate(currency_query):

    url = API_URL + "?q=" + currency_query + "&compact=ultra&apiKey=" + API_KEY
    log.info(" URL: %s", url)

    response = requests.get(url)

    return response

@api.route('/pinning-violation/report/<string:uid>', methods=['GET'])
def show_pinning_violation_report(uid):

    log.info("REQUEST CACHED UID: %s", uid)

    pinning_violation = _cache_pinning_violation_report.get(uid)

    if not pinning_violation:
        log.info("DELETED CACHED UID: %s", uid)
        _cache_pinning_violation_report.delete(uid)
        abort(make_response(jsonify(error = "Already expired the cache for uid: " + uid), 403))

    return jsonify(pinning_violation)

@api.route('/pinning-violation/report', methods=['POST'])
def collect_pinning_violation_report():

    json_request = json.dumps(request.get_json())
    content_length = len(json_request)

    if content_length is None:
        log.error("REQUEST WITHOUT CONTENT LENGTH: %i", content_length)
        abort(make_response(jsonify(), 400))

    if content_length > REPORT_MAX_LENGTH:
        log.error("REQUEST WITH CONTENT LENGTH TO BIG: %i", content_length)
        abort(make_response(jsonify(), 400))

    uid = hashlib.sha256(json_request.encode()).hexdigest()

    cached_report_url = request.base_url + "/" + uid
    log.info("CACHED REPORT URL: %s", cached_report_url)

    _cache_pinning_violation_report(uid)

    return jsonify(cached_report_url = cached_report_url)

# Cache for 5 minutes no more then 30 reports.
@lru(expire = 300, maxsize = 30)
def _cache_pinning_violation_report(uid):

    log.info("CACHED CERTIFICATE PINNING VIOLATION: %s", uid)

    pinning_violation = request.get_json()

    return pinning_violation
