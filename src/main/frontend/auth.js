import jwtDecode from 'jwt-decode'

const ACCESS_TOKEN_KEY = 'access_token'
const SECURITY_ENABLED_KEY = 'securityEnabled'

export function manageAuth (configuration) {
  window.localStorage.setItem(SECURITY_ENABLED_KEY, configuration.securityEnabled)

  if (!configuration.securityEnabled) {
    return
  }

  if (window.location.hash.includes('access_token=')) {
    setAccessToken()
    window.location = window.location.href.split('?')[0]
  }

  if (!isLoggedIn()) {
    login(configuration)
  }
}

export function getUserName () {
  if (isSecurityEnabled() && isLoggedIn()) {
    const token = jwtDecode(getAccessToken())
    return token.full_name
  }
  return 'anonymous'
}

export function getAccessToken () {
  return window.localStorage.getItem(ACCESS_TOKEN_KEY)
}

export function isLoggedIn () {
  const idToken = getAccessToken()
  return !!idToken && !isTokenExpired(idToken)
}

function isSecurityEnabled () {
  return window.localStorage.getItem(SECURITY_ENABLED_KEY) && window.localStorage.getItem(SECURITY_ENABLED_KEY) !== 'false'
}

function login (configuration) {
  console.log('login(), configuration', configuration)
  const redirectUri = encodeURIComponent(window.location.href + '?')
  const oauthUri = configuration.userAuthorizationUri
  const clientId = configuration.oauthClientId
  window.location = `${oauthUri}?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=token`
}

function getParameterByName (name) {
  let match = RegExp('[#/&]' + name + '=([^&]*)').exec(window.location.hash)
  return match && decodeURIComponent(match[1].replace(/\\+/g, ' '))
}

function setAccessToken () {
  const encodedToken = getParameterByName('access_token')
  const token = jwtDecode(encodedToken)
  console.log('got fresh OAuth token', token)
  window.localStorage.setItem(ACCESS_TOKEN_KEY, encodedToken)
}

function getTokenExpirationDate (encodedToken) {
  const token = jwtDecode(encodedToken)
  if (!token.exp) {
    return null
  }
  const date = new Date(0)
  date.setUTCSeconds(token.exp)
  return date
}

function isTokenExpired (token) {
  const expirationDate = getTokenExpirationDate(token)
  return expirationDate < new Date()
}
