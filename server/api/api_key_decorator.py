# System packages
import logging
from os import getenv
from functools import wraps
from flask import request, abort, make_response, jsonify

logging.basicConfig(level=logging.DEBUG)
log = logging.getLogger(__name__)

MOBILE_API_KEY = getenv("MOBILE_API_KEY")

log.info("MOBILE_API_KEY: " + MOBILE_API_KEY)

def check_api_key(callback):

    @wraps(callback)
    def decorated(*args, **kwargs):

        api_key = request.headers.get("Api-Key")

        if api_key is None:
            log.info("Missing API Key.")
            abort(make_response(jsonify({}), 400))

        elif api_key != MOBILE_API_KEY:
            log.info("Wrong API Key. Header Api-Key: " + api_key)
            abort(make_response(jsonify({}), 400))

        else:
            return callback(*args, **kwargs)

    return decorated
