# fancoil-example

Some examples for fancoil framework

## Introduction

This repo contains the following examples

- [simple] 
    - Originally copied from [day8/re-frame]
    - Use [reagent] ratom as db

- [simple-html-router]
	- Originally copied from [reagent-template]
	- This is a simple example of [fancoil.module.reitit/html-router]

- [todomvc-ratom]
	- Originally copied from [day8/re-frame]
	- Use [reagent] ratom as db
	- local storage

- [todomvc-posh]
	- Originally copied from [denistakeda/re-posh]
	- fancoil.module.posh: Use poshed [datascript] as db, use [posh] in subscribe

- [catchat]
	- Originally copied from [tonsky/datascript-chat]
	- fancoil.module.datascript: Use [datascript] as db, [rum] as view
    - Mock api, frontend only

- [catchat-full]
	- Move mock api part to server side
	- User defined module: Add [cljs-http] as http client and [haslett] as websocket client
	- Use [duct], [compojure] and [httpkit] in backend

- [realworld]
	- [Realworld] is a Medium.com like demo app
	- Originally copied from [jacekschae/conduit]
	- Use [fancoil.module]: fancoil.module.cljs-ajax, fancoil.module.reitit/html-router


[simple-html-router]:https://github.com/itarck/fancoil-example/tree/main/simple_html_router
[reagent-template]:https://github.com/reagent-project/reagent-template
[fancoil.module.reitit/html-router]:https://github.com/itarck/fancoil.module/tree/main/src/main/fancoil/module

[realworld]:https://github.com/itarck/fancoil-example/tree/main/realworld
[Realworld]:https://github.com/gothinkster/realworld
[jacekschae/conduit]:https://github.com/jacekschae/conduit
[fancoil.module]:https://github.com/itarck/fancoil.module/tree/main/src/main/fancoil/module

[day8/re-frame]:https://github.com/day8/re-frame/tree/master/examples
[denistakeda/re-posh]:https://github.com/denistakeda/re-posh/tree/master/examples/todomvc
[tonsky/datascript-chat]:https://github.com/tonsky/datascript-chat
[reagent]:https://github.com/reagent-project/reagent
[posh]:https://github.com/denistakeda/posh
[datascript]:https://github.com/tonsky/datascript
[rum]:https://github.com/tonsky/rum
[cljs-http]:https://github.com/r0man/cljs-http
[haslett]:https://github.com/weavejester/haslett
[duct]:https://github.com/duct-framework/duct
[compojure]:https://github.com/weavejester/compojure
[httpkit]:https://github.com/http-kit/http-kit
[simple]:https://github.com/itarck/fancoil-example/tree/main/simple
[todomvc-ratom]:https://github.com/itarck/fancoil-example/tree/main/todomvc-ratom
[todomvc-posh]:https://github.com/itarck/fancoil-example/tree/main/todomvc-posh
[catchat]:https://github.com/itarck/fancoil-example/tree/main/catchat
[catchat-full]:https://github.com/itarck/fancoil-example/tree/main/catchat-full