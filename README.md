# Matsuri-Engine
A simple Visual Novel Engine for Android

## About
这本来是一个完整的小游戏，考虑到版权问题，本 Repo 中删除了所有 立绘、cg、bgm 资源（cg只留了 title 界面的那张），只保留了脚本文件 app\src\main\assets\mainScript.txt（使用到的资源名都在脚本文件中）。  
完整的小游戏在 Release 中，使用到的资源：
+ CG & 立绘：《Re：LieF ～親愛なるあなたへ～》
+ BGM：天門 的各种为 Minori 写的 BGM
+ 剧本：《华胥心音》的前半段

## Script Ussage
Visual Novel 的功能都可以通过编辑 app\src\main\assets\mainScript.txt 实现（本来是想做成简单的黄油形式的，但是懒= =，所以就没选项了）。
```
* 脚本支持四种指令 ps: ( ) 中的代表具体的内容：
*      [MSG:(null | msgName)_(msgContent)]     在 textBox 上输出一条剧本消息 (null | msgName) 代表剧本的叙述者（null 为无，一般代表旁白之类的），(msgContent) 为具体的剧本内容
*      [CG:(cgName)]   显示 cg (cgName) 为 cg 在 assets 中的文件名（不包括后缀），格式要求为 jpg
*      [BGM:(bgmName)] 播放 bgm (bgmName) 为 音频文件 在 assets 中的文件名（不包括后缀），格式要求为 mp3
*      [+(spName)_(spPos)] 显示立绘 (spName) 为 立绘 在 assets 中的文件名（不包括后缀），格式要求为 png，(spPos) 为立绘位置，可以是 0 1 2 （代表 左 中 右）
* 其中 MSG 和 + 指令为瞬时指令（只在这次 OnClick 中有效，下一次 OnClick 时会将之前的信息全清空）；CG 和 BGM 指令为持续指令（直到下一次使用同类指令时上一个指令才失效）
* 每一行代表一次 OnClick 要执行的所有指令
```
更详细的说明参见 app\src\main\java\cn\edu\cuc\kuroki\matsuri\ui\fragment\GalFragment.java 中的 ScriptController 类。
