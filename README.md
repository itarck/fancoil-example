# fancoil-example
Some examples for fancoil framework

## Introduction

Contains the following examples

- simple 
    - Originally copied from [day8/re-frame]
    - Use [reagent] ratom as db
- todomvc-ratom
	- Originally copied from [day8/re-frame]
	- Use [reagent] ratom as db
- todomvc-datascript:
	- Originally copied from [denistakeda/re-posh]
	- Use [datascript] as db, use [posh] in subscribe
- catchat
	- Originally copied from [tonsky/datascript-chat]
	- Use datascript as db, [rum] as view
    - Mock api, frontend only


[day8/re-frame]:https://github.com/day8/re-frame/tree/master/examples
[denistakeda/re-posh]:https://github.com/denistakeda/re-posh/tree/master/examples/todomvc
[tonsky/datascript-chat]:https://github.com/tonsky/datascript-chat
[reagent]:https://github.com/reagent-project/reagent
[posh]:https://github.com/denistakeda/posh
[datascript]:https://github.com/tonsky/datascript
[rum]:https://github.com/tonsky/rum

## How to use

in example folder 

```
yarn
npx shadow-cljs watch app

```
