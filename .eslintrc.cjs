module.exports = 
{
  root: true,
  extends: [
    'eslint:recommended',
    'plugin:@typescript-eslint/recommended-type-checked',
    'plugin:@typescript-eslint/stylistic-type-checked',
    'plugin:sonarjs/recommended'
  ],
  "ignorePatterns": ["build/**", "vitest.config.ts"],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    project: './tsconfig.json',
    ecmaFeatures: {
      jsx: false
    }
  },
  plugins: ['@typescript-eslint', 'import', 'sonarjs'],
  rules: {
    '@typescript-eslint/ban-ts-comment': [
      'error',
      {'ts-expect-error': 'allow-with-description'},
    ],
    "no-unused-vars": "off",
    '@typescript-eslint/no-unused-vars': [
      'warn',
      { "args":"none","caughtErrors":"none" }
    ],
    'no-throw-literal': 'off',
    "@typescript-eslint/no-throw-literal": "error",
    '@typescript-eslint/no-non-null-assertion': 'error',
    '@typescript-eslint/explicit-function-return-type': 'error',
    "no-use-before-define": "off",
    "@typescript-eslint/no-use-before-define": "error",
    "@typescript-eslint/no-inferrable-types": "off",
    "@typescript-eslint/unbound-method": "off",
    "@typescript-eslint/prefer-optional-chain":"off",
    'max-len': [
      'error',
      {
        code: 130,
        ignoreComments: true,
        ignoreStrings: true,
        ignoreTemplateLiterals: true
      }
    ]
  }
}
