import { defineUserConfig, defaultTheme } from 'vuepress'
import { getDirname, path } from '@vuepress/utils'
import { registerComponentsPlugin } from '@vuepress/plugin-register-components'
import { prismjsPlugin } from '@vuepress/plugin-prismjs'
import { searchPlugin } from '@vuepress/plugin-search'
import { sidebarConfig } from './configs/sidebar'
import { navbarConfig } from './configs/navbar'
import Prism from 'prismjs';
import definePrismOwnLang from '../../../editors/prismjs/own-language.js'

definePrismOwnLang(Prism)
const __dirname = getDirname(import.meta.url)

export default defineUserConfig({
  base: "/docs/ownlang/",
  locales: {
    '/en/': {
      lang: 'en-US',
      title: 'OwnLang',
      description: 'OwnLang documentation',
    },
    '/ru/': {
      lang: 'ru-RU',
      title: 'OwnLang',
      description: 'Документация OwnLang',
    }
  },

  theme: defaultTheme({
    repo: 'aNNiMON/Own-Programming-Language-Tutorial',
    docsBranch: 'next',
    editLinkPattern: ':repo/blob/:branch/docs/docs/:path',
    editLinkText: 'View source',
    contributors: false,
    locales: {
      '/en/': {
        selectLanguageName: 'English',
        sidebar: sidebarConfig.en,
        navbar: navbarConfig.en
      },
      '/ru/': {
        selectLanguageName: 'Русский',
        sidebar: sidebarConfig.ru,
        navbar: navbarConfig.ru
      }
    }
  }),

  plugins: [
    prismjsPlugin({
      preloadLanguages: ['own', 'json']
    }),
    registerComponentsPlugin({
      componentsDir: path.resolve(__dirname, './components'),
    }),
    searchPlugin({
      locales: {
        '/en/': { placeholder: 'Search' },
        '/ru/': { placeholder: 'Поиск' },
      },
    }),
  ],
})
