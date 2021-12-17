# catchat-full

- Move mock api part to server side
- Add [cljs-http] as http client and [haslett] as websocket client
- Use [duct], [compojure] and [httpkit] in backend


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



### How to use

Start backend

you can read duct [Getting Started] documents for reference.

[Getting Started]:https://github.com/duct-framework/duct/wiki/Getting-Started#starting-the-system

```
lein repl
user=> (dev)
dev=> (go)

```


Start frontend

```
yarn
yarn shadow-cljs watch app
```

View http://localhost:3000


<img src="https://github.com/itarck/fancoil-example/blob/main/catchat/ScreenShot.png" width="600">