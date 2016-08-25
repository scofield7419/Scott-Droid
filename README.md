# Scott'Droid
### `Android music player.`  `炫酷黑.`  一个精简至极的`android music player.`
***
## 一.简介
1. 这实在是一个“很久远”的项目了，因为时间的问题，这个项目玩到一半就没能继续了，不过现在还是搬上来更大家分享一下，最重要的是记录一下自己的一些东西吧。希望以后能继续补充完善，打造成一个自己最初想实现的应用系统。
2. ps，我在最后一部分补充了将来将要去完善的东西。也同时欢迎各位亲的`star`和`pull request`哈O(∩_∩)O
3. 整体以黑色调为主题，全面模仿魅族flyme自带音乐播放器，界面的整洁度是我追求的出发点。
4. 除了作为一个轻量级音乐播放器，我还加入了一个`rapper`部分，实现了我以前的愿望：能学个rap```````^_^
5. 实现这个player的过程中，没有加入任何花哨的技术，没有使用任何三方框架，一切都是用自己能做到的代码实现。以此来检验自己当时的学习水平。
6. 不废话了，哈哈，放图

### 播放主界面
![main](https://github.com/scofield7419/Sample46/blob/master/screenshot/S60825-220430.jpg)
### 所以说全程模仿MX music player 嘛·······
![main](https://github.com/scofield7419/Sample46/blob/master/screenshot/S60825-220345.jpg)
### 歌词界面
![main](https://github.com/scofield7419/Sample46/blob/master/screenshot/S60825-220425.jpg)
### 播放列表界面
![main](https://github.com/scofield7419/Sample46/blob/master/screenshot/S60825-220436.jpg)

### rap界面
![main](https://github.com/scofield7419/Sample46/blob/master/screenshot/S60825-220327.jpg)

![main](https://github.com/scofield7419/Sample46/blob/master/screenshot/S60825-220333.jpg)

### 设置界面···是不是又抄袭了MX music player
![main](https://github.com/scofield7419/Sample46/blob/master/screenshot/S60825-220958.jpg)

![main](https://github.com/scofield7419/Sample46/blob/master/screenshot/S60825-221013.jpg)

### 自定义一个vertical progressBar 的时候在android 5.x上遇到一个bug,求大拿提提建议
### 问题：滑动前显示正常，
![main](https://github.com/scofield7419/Sample46/blob/master/screenshot/S60620-134729.jpg)
### 一旦滑动bar，进度就紊乱了······心塞，这个问题貌似目前没有解决方案·····貌似···
![main](https://github.com/scofield7419/Sample46/blob/master/screenshot/S60620-134719.jpg)

### 好了，主要的界面大概就是介个样子，更多细节等待您去解锁，接下来详细总结一下。


## 二.已实现功能点述
### 1.播放部分
1. 两个`service`开启播放服务，而多个组件之间的数据交互是通过`local broadcast`，比如多个fragment之间的视图更新以及同步，而使用`local broadcast` 是为了防止广播全局污染。
2. 实现`mediaplayer`的播放时还有很多的小细节问题，比如，监听电话接听、监听耳机插拔情况、监听系统mediaplayer焦点的得失情况等等
3. 一个`player service` 作为主播放器，另外一个`player service `用来作为`rapper`的播放。
4. 整个app只有一个`activity`，`three fragment is all.` 
5. app的歌词展示部分是参考了网上一个案例，然后自己改造了一番。
6. 音乐均衡器的实现在新机型上可以支持，但对于一些老的机型可能不完美支持了。

### 2.UI部分
1. 整体的配色方案是完全参考MX 系统音乐播放器的，真心感觉到魅族播放器小组是花了一定的心思的，很明显他的button以及其他的东西都有经过用心的调优，才能带给用户如此好的体验。UI图片资源一部分取自于自带apk，一部分来自于自己的PS。
2. 其他的滑动界面属于自定义属于自定义view，另外，播放列表的索引部件也是参考了网上资料然后自己改造了一番。
3. 还有一些动画效果用了`tween animation` & `property animator`.


## 三.待实现功能点述
  整体来说，因为时间的不足，这个项目就像是一个阑尾工程，总是有一些漏洞需要去完善（(⊙﹏⊙)b），不少地方都需要去重构的。但无论怎样，我多希望先分享出来，再等哪天有空了，一一完善呗·······

1. 实现桌面、锁屏界面、`notification`&`statusBar`的歌曲播放同步。
2. 实现网络下载歌词。
3. `material design`
4. 用`MVP`/`Event bus`/`ORMLite`/`fresco`等等去包装一下。
5. 与将来构思的一个音乐站点（本人音乐鉴赏与推荐）关联，实现在线歌曲推。
6. 同时把用户模块、社区整出来。
7. ········期待您的参与，您的`star`&`pull request`使`scott' droid`熠熠生辉！




***
```
Scofield.Phil

Email: feish7419@163.com

move fast, break things.
```





