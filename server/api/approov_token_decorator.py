# System packages
import logging
from os import getenv
from functools import wraps
from flask import request, abort, make_response, jsonify
from base64 import b64decode
import jwt
import json

logging.basicConfig(level=logging.DEBUG)
log = logging.getLogger(__name__)

APPROOV_BASE64_SECRET = getenv('APPROOV_BASE64_SECRET')

def check_approov_token(callback):

    # Function to check the validity of the token
    def verifyToken(token):

        try:
            # Decode our token, allowing only the HS256 algorithm, using our base64 encoded SECRET
            token_claims = jwt.decode(token, b64decode(APPROOV_BASE64_SECRET), algorithms=['HS256'])
            log.info('APPROOV TOKEN CLAIMS: ' + json.dumps(token_claims, indent=4))
            return token_claims
        except jwt.InvalidSignatureError as e:
            log.info('APPROOV JWT TOKEN INVALID SIGNATURE: %s' % e)
            abort(make_response(jsonify({}), 401))
        except jwt.ExpiredSignatureError as e:
            log.info('APPROOV JWT TOKEN EXPIRED: %s' % e)
            abort(make_response(jsonify({}), 401))
        except jwt.InvalidTokenError as e:
            log.info('APPROOV JWT TOKEN INVALID: %s' % e)
            abort(make_response(jsonify({}), 401))

    @wraps(callback)
    def decorated(*args, **kwargs):

        token = request.headers.get("Approov-Token")
        log.info("Approov-Token: " + token)

        if not token:
            log.info('Missing Approov-Token in the request headers.')
            abort(make_response(jsonify({}), 400))

        token_claims = verifyToken(token)

        if not token_claims:
            log.info('Approov token claims cannot be empty.')
            abort(make_response(jsonify({}), 400))

        return callback(*args, **kwargs)

    return decorated


