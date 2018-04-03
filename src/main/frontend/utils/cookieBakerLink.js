import { cookieBakerHost } from './cookieBakerHost'

export function cookieBakerLink (experimentId, variantName, preserveOther) {
  let host = cookieBakerHost()
  let other = preserveOther === true ? '' : ',-'
  return `https://${host}/chi/cookie-baker.html?chi=${experimentId}!${variantName}${other}&redirect=${`https://${host}/`}`
}
