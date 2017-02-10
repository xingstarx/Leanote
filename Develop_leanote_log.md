# 开发日志
Leanote android app

### 首次同步规则
同步代码放在SyncService这个服务中，当第一次同步的时候，这个时候应该是网络请求，保存数据库操作混杂在一起。

逻辑如下：

1. 先获取到usn的值
2. 获取所有的笔记本(notebook/getNotebooks)，不使用同步的方法(notebook/getSyncNotebooks),担心同步的方法，存在数据冗余
3. 遍历每一个笔记本，获取笔记本下面的所有笔记，然后保存，同样不使用同步方法

