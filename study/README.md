### 原生项目引入Flutter module，三种通讯方式示例

> 目录说明
> - study 原生项目
> - tson_flutter 引入的flutter module

### 如何在原生项目添加flutter的module？

- 创建`Flutter module`
> 菜单： File > New > New Flutter Project，然后如下填写你的项目信息

![image1](https://github.com/xintanggithub/studyDemo/blob/main/study/newflutterproject.png?raw=true)

- 下载 `Android Sdk Command Tools`，如下

![image2](https://github.com/xintanggithub/studyDemo/blob/main/study/downloadtoolssdk.png?raw=true)

- 根目录 `settings.gradle` 修改入下，注意看注释
```gradle
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT) // 这里要修改为RepositoriesMode.PREFER_PROJECT
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "study"
include ':app'

// 这里是新增的 ↓↓↓↓↓↓↓↓ (如果编译器提示Binding需要导包，不要管，报错不影响构建)
setBinding(new Binding([gradle: this]))
evaluate(new File(
        settingsDir.parentFile,
        'tson_flutter/.android/include_flutter.groovy'
))
// 上面是新增的 ↑↑↑↑↑↑↑↑

// 下面这个不用管，上面的添加完，构建成功之后会自动生成
include ':tson_flutter'
project(':tson_flutter').projectDir = new File('../tson_flutter')
```

然后构建，构建成功之后会自动生成下面两行配置

- `gradle.properties` 中添加 `android.enableJetifier=true`

- 最后在 `app` 下的 `build.gradle` 添加依赖
> 注意是 `':flutter'` 而不是 `':tson_flutter'` (不是上面自己创建的时候取的 `module` 名字)
```gradle
implementation project(':flutter')
```
然后就可以正常运行了

### 原生和Flutter之间如何进行通讯？

