
export function cookieBakerHost () {
  let host = {
    'chi.allegrogroup.com': 'allegro.pl'
  }[window.location.hostname]

  if (!host) {
    return 'allegro.pl.allegrosandbox.pl'
  }

  return host
}
