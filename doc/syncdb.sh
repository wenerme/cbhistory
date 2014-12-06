#! /bin/env bash

# 该脚本主要用于通过 ssh 进行远程数据库同步
# 使用的是 导出-压缩-上传-解压-导入 的过程,而不是直接连接远程数据库同步
# 因为直接使用远程数据库同步会非常慢, 而且导致表被锁住不能操作.

# mysqldump 手册页面
# http://dev.mysql.com/doc/refman/5.7/en/mysqldump.html
# --no-data 只导出结构

# 导入
# mysql -uroot -p --default-character-set=utf8 database
# mysql> SOURCE utf8.dump
# 或
# mysql -e"utf8.dmp"

# gzip -cf cbh.mysql
# 使用的 7z 来压缩, 为了节省宽带和加快速度 :-)
# 
SSH_INFO=wener@wen

mysqldump -uroot -proot --hex-blob --complete-insert cbh -r cbh.mysql
rm cbh.7z
7za a cbh.7z cbh.mysql
scp cbh.7z $SSH_INFO:~/work
ssh $SSH_INFO "7za x -y ~/work/cbh.7z -o ."
ssh $SSH_INFO "mysql -uroot -proot cbh < ~/work/cbh.mysql"
echo DONE
