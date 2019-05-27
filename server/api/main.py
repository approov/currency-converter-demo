# System packages
import logging
import requests
import time
import json

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

    currency_query = from_currency + "_" + to_currency

    response = _get_conversion_rate(currency_query)

    try:
        json_response = response.json()
    except json.decoder.JSONDecodeError as e:
        log.error("Invalid JSON in the response | %s", e)
        _get_conversion_rate.delete(currency_query)
        return jsonify(error="Currency Rates Server timeout. Please try again later.")

    if currency_query in json_response:
        conversion_rate = json_response[currency_query]
        log.info(" CONVERSION RATE: %f", conversion_rate)

        converted_value = conversion_rate * float(value_to_convert)
        log.info(" CONVERTED VALUE: %f", converted_value)

        converted_value = format_currency(converted_value, currency = to_currency)

        return jsonify(converted_value=converted_value)

    if not json_response:
        log.error("CURRENCY CONVERTER FREE API ERROR: Check if the currency codes are valid.")
        return jsonify(error="Check if the currency codes are valid.")

    if "error" in json_response:
        log.error("CURRENCY CONVERTER FREE API ERROR: " + json_response["error"])
        return jsonify(error="Reached free API limit. Please retry later.")

@lru(expire=3600)
def _get_conversion_rate(currency_query):

    url = API_URL + "?q=" + currency_query + "&compact=ultra&apiKey=" + API_KEY
    log.info(" URL: " + url)

    response = requests.get(url)

    return response
