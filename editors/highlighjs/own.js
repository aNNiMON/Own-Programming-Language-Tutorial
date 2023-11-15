export default function(hljs) {
  const STRING = {
    className: 'string',
    variants: [{
      begin: '"', end: '"',
      contains: [hljs.BACKSLASH_ESCAPE]
    }]
  };

  const EXTENDED_LITERAL = {
    className: 'literal',
    variants: [{
      begin: '`', end: '`',
      illegal: '\\n'
    }]
  };

  const METHOD = {
    className: 'function',
    beginKeywords: 'def',
    end: /[:={\[(\n;]/,
    excludeEnd: true,
    contains: [{
      className: 'title',
      begin: /[^0-9\n\t "'(),.`{}\[\]:;][^\n\t "'(),.`{}\[\]:;]+|[^0-9\n\t "'(),.`{}\[\]:;=]/,
      relevance: 0
    }]
  };

  return {
    keywords: {
      literal: 'true false this null',
      keyword: 'break class continue def else for if match print println return use while do case extract include'
    },
    contains: [
      hljs.C_LINE_COMMENT_MODE,
      hljs.C_BLOCK_COMMENT_MODE,
      STRING,
      EXTENDED_LITERAL,
      METHOD,
      hljs.C_NUMBER_MODE
    ]
  };
};