# Syntax for Editors

## Intellij IDEA

1. Open an `idea` folder
2. Add all files and folders to zip archive, e.g. `settings.zip`
3. File -> Manage IDE Settings -> Import. Select your zip file.

## Prism.js

```javascript
import Prism from 'prismjs';
import definePrismOwnLang from './prismjs/own-language.js'
definePrismOwnLang(Prism)
```
