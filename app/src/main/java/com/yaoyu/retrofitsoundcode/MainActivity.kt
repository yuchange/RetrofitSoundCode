package com.yaoyu.retrofitsoundcode

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        /**
         * 源码详解
         * 1. validateServiceInterface(Class<?> service)
         *  此处分三部分
         *  1) 判断传进来的service类是不是接口,如果不是就报错
         *  2)创建一个双向队列一层层将类与类的父类等依次取出来进行拼接,此处会判断当前类或父类是不是一个含有泛型的接口
         *  3)validateEagerly(boolean)  此处通过此参数进行对每个方法进行加载,进行遍历初始化,来激进检查合法性,此处建议为debug功能
         * 2.动态代理
         *  return (T) ......
         *  将传入进来的类,进行运行时动态加载, InvocationHandler.
         *  1)通过判断传进来的参数是否为接口中的方法,如果是object方法即直接返回(兼容性) (Retrofit 143)
         *  2)判断是否为java8平台的默认方法,若是即直接返回  (Retrofit 146)
         *  3)最后返回loadServiceMethod().invoke(),loadServiceMethod()此处是一个带缓存的加载,其中核心是没有取到缓存,然后进行重新加载
         *    其中使用ServiceMethod来实现外部的invoke方法
         * 3.难点
         *   call
         *   adapter 中有一个线程池,用来切到后台,获取数据后,再切到主线程  还有一个职能是接通RXJAVA,通过对callAdapter进行遍历来匹配配置,通过对RXJAVA的一些配置符进行返回相应的call
         *   requestFactory 来拼接注解 参数等用来配置后续的网络请求
         *
         *
         *
         *
         *
         *  parseResponse 来解析okhttp返回的数据
         *
         *
         *
         *
         *
         *
         */
        val service = retrofit.create(GitHubService::class.java)

        val repos: Call<List<Repo>> = service.listRepos("octocat")
        //此方法的异步的  execute()为同步的  此处多个请求会同时发生 当请求过多时会出现等待现象
        repos.enqueue(object : Callback<List<Repo>>{
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                println("Respose ${response.body()!![0].name}")
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {

            }

        })


    }
}