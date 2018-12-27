export function containsNoSpecialCharacters (str) {
  if (!str) {
    return true
  }
  let chars = ['*', '?', '%', '\'', '/', '\\', '"']

  return chars.indexOf(str) === -1 || `no ${str}`
}
