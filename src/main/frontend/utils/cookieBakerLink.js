import { cookieBakerHost } from './cookieBakerHost'

export function cookieBakerLink (experimentId, variantName) {
  let protocol = 'https://'
  let host = cookieBakerHost()
  return protocol + host + `/chi/cookie-baker.html?chi=${experimentId}!${variantName}&redirect=${protocol + host + '/'}`
}
