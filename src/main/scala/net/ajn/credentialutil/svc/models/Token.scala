package net.ajn.credentialutil.svc.models

import java.time.LocalDateTime

case class Token(value: String, validUntil: LocalDateTime, tokenType: String)
