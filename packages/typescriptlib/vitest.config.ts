import path from "path"
import {defineConfig} from 'vitest/config'

//  FIXME - Path aliases defined both here and in tsconfig
export default defineConfig(
    {test : {include : [ "src/**/*.test.ts" ]}, resolve : {alias : {"@src" : path.resolve(__dirname, "src/main")}}})