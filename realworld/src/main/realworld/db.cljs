(ns realworld.db)


(def sample-db
  {:user {:username "itarck", :bio "hello"
          :image "https://avatars.githubusercontent.com/u/2196950?v=4"
          :email "itarck@gmail.com"
          :password "jlkfjdsff"
          :following false
          :token "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJpdGFyY2siLCJpc3MiOiJpdGFyY2tAZ21haWwuY29tIiwiZXhwIjoxNjQxMzA1ODY4LCJpYXQiOjE2NDA3MDEwNjh9.yhvuXPZuQflqLgwN4_4hwFFnAM3gvCt2z7AUD5bI4js"}
   :pages {"/" {:loading {:articles false
                          :tags false}
                :filter {:offset 0, :limit 10}
                :articles [{:description "lkjsdf"
                            :slug "hello"
                            :updatedAt "2021-12-30T15:10:24.828Z"
                            :createdAt "2021-12-30T15:10:24.828Z"
                            :title "hello"
                            :author
                            {:username "tizzac", :bio nil, :image nil, :following false}
                            :favoritesCount 0
                            :id 33
                            :body "lsjflslsf"
                            :favorited false
                            :tagList ["fff"]}
                           {:description "aaa"
                            :slug "aaa"
                            :updatedAt "2021-12-30T08:44:39.068Z"
                            :createdAt "2021-12-30T08:44:39.068Z"
                            :title "aaa"
                            :author
                            {:username "itarck", :bio nil, :image nil, :following false}
                            :favoritesCount 0
                            :id 32
                            :body "aaa"
                            :favorited false
                            :tagList ["aaa"]}
                           {:description "lksjfl"
                            :slug "lksjd"
                            :updatedAt "2021-12-30T08:39:27.662Z"
                            :createdAt "2021-12-30T08:39:27.662Z"
                            :title "lksjd"
                            :author
                            {:username "itarck", :bio nil, :image nil, :following false}
                            :favoritesCount 0
                            :id 31
                            :body "lkjfdlk"
                            :favorited false
                            :tagList ["ff"]}
                           {:description "ffff"
                            :slug "aaa-1640836329938"
                            :updatedAt "2021-12-30T03:52:09.938Z"
                            :createdAt "2021-12-30T01:13:30.507Z"
                            :title "aaa"
                            :author
                            {:username "itarck", :bio nil, :image nil, :following false}
                            :favoritesCount 0
                            :id 30
                            :body "bbb"
                            :favorited false
                            :tagList ["ff"]}
                           {:description "lskdjf"
                            :slug "lfkjds-1640823441887"
                            :updatedAt "2021-12-30T00:17:21.887Z"
                            :createdAt "2021-12-30T00:17:01.295Z"
                            :title "lfkjds"
                            :author
                            {:username "itarck", :bio nil, :image nil, :following false}
                            :favoritesCount 0
                            :id 29
                            :body "abcd"
                            :favorited false
                            :tagList ["ff" "gg"]}
                           {:description "sdfs"
                            :slug "sdf-1640762818608"
                            :updatedAt "2021-12-29T07:26:58.608Z"
                            :createdAt "2021-12-29T07:21:18.390Z"
                            :title "sdf"
                            :author
                            {:username "itarck", :bio nil, :image nil, :following false}
                            :favoritesCount 0
                            :id 28
                            :body "abc"
                            :favorited false
                            :tagList ["sdf"]}
                           {:description "sfd"
                            :slug "asdf"
                            :updatedAt "2021-12-29T07:17:12.464Z"
                            :createdAt "2021-12-29T07:17:12.464Z"
                            :title "asdf"
                            :author
                            {:username "itarck", :bio nil, :image nil, :following false}
                            :favoritesCount 0
                            :id 27
                            :body "sdf"
                            :favorited false
                            :tagList ["ff"]}
                           {:description "Ever wonder how?"
                            :slug "howtocode-1640665252496"
                            :updatedAt "2021-12-28T04:20:52.496Z"
                            :createdAt "2021-12-28T04:20:52.496Z"
                            :title "howtocode"
                            :author
                            {:username "itarck", :bio nil, :image nil, :following false}
                            :favoritesCount 0
                            :id 25
                            :body "You have to practice"
                            :favorited false
                            :tagList ["clojure" "code"]}
                           {:description "Ever wonder how?"
                            :slug "howtocode-1640665244531"
                            :updatedAt "2021-12-28T04:20:44.531Z"
                            :createdAt "2021-12-28T04:20:44.531Z"
                            :title "howtocode"
                            :author
                            {:username "itarck", :bio nil, :image nil, :following false}
                            :favoritesCount 1
                            :id 24
                            :body "You have to practice"
                            :favorited false
                            :tagList ["clojure" "code"]}
                           {:description "Ever wonder how?"
                            :slug "howtocode-1640665235592"
                            :updatedAt "2021-12-28T04:20:35.592Z"
                            :createdAt "2021-12-28T04:20:35.592Z"
                            :title "howtocode"
                            :author
                            {:username "itarck", :bio nil, :image nil, :following false}
                            :favoritesCount 1
                            :id 23
                            :body "You have to practice"
                            :favorited false
                            :tagList ["clojure" "code"]}]
                :articles-count 10
                :tags ["fff"
                       "reactjs"
                       "angularjs"
                       "dragons"
                       "clojure"
                       "code"
                       "ff"
                       "sdf"
                       "gg"
                       "aaa"]}}})


