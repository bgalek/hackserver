import { cookieBakerHost } from './cookieBakerHost'

export function cookieBakerLink (experimentId, variantName) {
  let host = cookieBakerHost()
  return `https://${host}/chi/cookie-baker.html?chi=${experimentId}!${variantName},-&redirect=${`https://${host}/`}`
}
