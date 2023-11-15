import pages from './pages'

let sidebar = {}
for (let lang of ['en', 'ru']) {
  let config = {}
  for (let [relativePath, entry] of Object.entries(pages)) {
    const path = '/' + lang + relativePath
    config[path] = (path in config) ? config[path] : []
    config[path].push({
      text: entry.text[lang],
      children: entry.pages.map(r => path + r)
    })
  }
  sidebar[lang] = config
}

export const sidebarConfig = sidebar