package net.ajn.credentialutil.svc.models

import java.time.LocalDateTime

case class Token(access_token: String, expires_in: Int, token_type: String)
