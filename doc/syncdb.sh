#! /bin/bash

# �ýű���Ҫ����ͨ�� ssh ����Զ�����ݿ�ͬ��
# ʹ�õ��� ����-ѹ��-�ϴ�-��ѹ-���� �Ĺ���,������ֱ������Զ�����ݿ�ͬ��
# ��Ϊֱ��ʹ��Զ�����ݿ�ͬ����ǳ���, ���ҵ��±���ס���ܲ���.

# mysqldump �ֲ�ҳ��
# http://dev.mysql.com/doc/refman/5.7/en/mysqldump.html
# --no-data ֻ�����ṹ

# ����
# mysql -uroot -p --default-character-set=utf8 database
# mysql> SOURCE utf8.dump
# ��
# mysql -e"utf8.dmp"

# gzip -cf cbh.mysql
# ʹ�õ� 7z ��ѹ��, Ϊ�˽�ʡ����ͼӿ��ٶ� :-)
# 
SSH_INFO=wener@wen

mysqldump -uroot -proot --hex-blob --complete-insert cbh -r cbh.mysql
rm cbh.7z
7za a cbh.7z cbh.mysql
scp cbh.7z $SSH_INFO:~/work
ssh $SSH_INFO "7za x -y ~/work/cbh.7z -o ."
ssh $SSH_INFO "mysql -uroot -proot cbh < ~/work/cbh.mysql"
echo DONE
