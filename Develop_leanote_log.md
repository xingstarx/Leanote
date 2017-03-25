# 开发日志
Leanote android app

### 首次同步规则
同步代码放在SyncService这个服务中，当第一次同步的时候，这个时候应该是网络请求，保存数据库操作混杂在一起。
当account 的usn 为0的时候，就意味着全量同步，使用全量的api,如果不为0那就是增量同步，获取到当前的usn以及服务器上最大的maxUsn, 这部分用增量的api 
单一文件的同步，同步某一个文件的时候，先pull, 再push,有冲突的话，逻辑按照Leamonax 的来

逻辑如下：

1. 先获取到usn的值
2. 获取所有的笔记本(notebook/getNotebooks)，不使用同步的方法(notebook/getSyncNotebooks),担心同步的方法，存在数据冗余
3. 遍历每一个笔记本，获取笔记本下面的所有笔记，根据笔记id，获取完整的笔记，然后保存（处理图片，附件等资源）只能一个一个的处理保存，而不能用数据库的事务来批量操作，同样不使用同步方法
4. 笔记中存在图片，附件等资源，note 与noteFile 做映射 单独管理图片，附件的需求

###文档目录结构

1. 笔记本下面对应的是笔记，目录结构的形式跟forkhub 的风格类似

### RxBus
使用RxBus作为事件总线

### XRecyclerView
用XRecyclerView 实现下拉刷新的功能

### NoteActivity 和 EditNoteActivity
预览笔记，包括预览富文本和markdown