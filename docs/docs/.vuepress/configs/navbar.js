import pages from './pages'

let navbar = {}
for (let lang of ['en', 'ru']) {
  let config = []
  for (let [relativePath, entry] of Object.entries(pages)) {
    const path = '/' + lang + relativePath
    config.push({
      text: entry.text[lang],
      children: entry.pages.map(r => path + r)
    })
  }
  navbar[lang] = config
}

export const navbarConfig = navbar