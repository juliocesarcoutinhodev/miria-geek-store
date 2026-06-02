import tsParser from '@typescript-eslint/parser';
import tsPlugin from '@typescript-eslint/eslint-plugin';
import angularPlugin from '@angular-eslint/eslint-plugin';
import angularTemplatePlugin from '@angular-eslint/eslint-plugin-template';
import angularTemplateParser from '@angular-eslint/template-parser';
import prettierPlugin from 'eslint-plugin-prettier';
import prettierConfig from 'eslint-config-prettier';

export default [
    {
        ignores: ['**/dist/**', '**/node_modules/**']
    },
    {
        files: ['**/*.ts'],
        languageOptions: {
            parser: tsParser,
            parserOptions: {
                project: ['tsconfig.json'],
                createDefaultProgram: true
            }
        },
        plugins: {
            '@typescript-eslint': tsPlugin,
            '@angular-eslint': angularPlugin,
            '@angular-eslint/template': angularTemplatePlugin,
            prettier: prettierPlugin
        },
        processor: angularTemplatePlugin.processors['extract-inline-html'],
        rules: {
            ...tsPlugin.configs.recommended.rules,
            '@angular-eslint/component-selector': ['error', { type: 'element', prefix: 'app', style: 'kebab-case' }],
            '@angular-eslint/directive-selector': ['error', { type: 'attribute', prefix: 'app', style: 'camelCase' }],
            '@angular-eslint/component-class-suffix': ['error', { suffixes: [''] }],
            '@angular-eslint/no-host-metadata-property': 'off',
            '@angular-eslint/no-output-on-prefix': 'off',
            '@typescript-eslint/no-explicit-any': 'off',
            '@typescript-eslint/no-inferrable-types': 'off',
            'arrow-body-style': ['error', 'as-needed'],
            curly: 0,
            'no-console': 0,
            'prefer-const': 0,
            'prettier/prettier': 'error',
            'padding-line-between-statements': [
                'error',
                { blankLine: 'always', prev: ['const', 'let', 'var'], next: '*' },
                { blankLine: 'any', prev: ['const', 'let', 'var'], next: ['const', 'let', 'var'] },
                { blankLine: 'any', prev: ['case', 'default'], next: 'break' },
                { blankLine: 'any', prev: 'case', next: 'case' },
                { blankLine: 'always', prev: '*', next: 'return' },
                { blankLine: 'always', prev: 'block', next: '*' },
                { blankLine: 'always', prev: '*', next: 'block' },
                { blankLine: 'always', prev: 'block-like', next: '*' },
                { blankLine: 'always', prev: '*', next: 'block-like' },
                { blankLine: 'always', prev: ['import'], next: ['const', 'let', 'var'] }
            ]
        }
    },
    {
        files: ['**/*.html'],
        languageOptions: {
            parser: angularTemplateParser
        },
        plugins: {
            '@angular-eslint/template': angularTemplatePlugin
        },
        rules: {
            '@angular-eslint/template/banana-in-box': 'error',
            '@angular-eslint/template/eqeqeq': ['error', { allowNullOrUndefined: true }],
            '@angular-eslint/template/no-negated-async': 'error'
        }
    },
    prettierConfig
];
