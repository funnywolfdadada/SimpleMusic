# SimpleMusic
  初学Android的练手作品，涉及到的主要内容有：
  
  1、一个Activity用于显示界面，上面有两个Fragment，一个显示音乐列表，一个显示音乐的控制板，两个Fragment通过Activity的回调通讯;
  
  2、Service用于后台播放音乐，通过本地的Binder实现的接口被Activity控制;
  
  3、前台Service用到通知，利用RemoteView显示音乐基本信息，同时可以通过广播对Service进行基本的控制;
  
  4、ContentResolver用于扫描所有mp3文件生成列表;
  
  5、Glide用于设置来自图库或者文件的背景图片;
  
  6、OKhttp用于下载Bing的今日美图，并使用Glide设置背景。
  
  功能比较简单，启动时会自动搜索手机上所有mp3文件，生成一个“所有歌曲”的歌单;支持创建、删除和重命名歌单;支持歌单歌曲（“所有歌曲”除外）的添加删除;支持图库和Bing美图来设置背景;支持通知栏显示歌曲信息，控制歌曲上一曲、暂停/播放和下一曲
  
  暂未实现对耳机插拔和音乐焦点事件的处理。
  
<div align="center">
<img width="30%" height="30%" src="/SimpleMusicScreenshot1.jpg">
<img width="30%" height="30%" src="/SimpleMusicScreenshot2.jpg">
<img width="30%" height="30%" src="/SimpleMusicScreenshot3.jpg">
</div>
