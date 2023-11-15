export default function(Prism) {
  Prism.languages.own = Prism.languages.extend('clike', {
    'string': {
      pattern: /(^|[^\\])"(?:\\.|[^"\\])*"/,
      lookbehind: true,
      greedy: true
    },
    'keyword': /\b(?:break|case|class|continue|def|do|else|extract|for|if|include|match|new|print|println|return|while|use)\b/,
    'function': {
      pattern: /((?:^|\s)def\s*)([a-zA-Z_]\w*)?(?=\s*\()/g,
      lookbehind: true
    },
    'operator': {
      pattern: /(^|[^.])(?:<<=?|>>>?=?|->|--|\+\+|&&|\*\*|\|\||::|\.\.\.?|[?:~]|[-+*/%&|^!=<>]=?)/m,
      lookbehind: true
    },
    'punctuation': /[{}[\];(),.:`]/
  });
}