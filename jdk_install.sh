#!/bin/bash
JAVA_HOME=''
jdkDir=/usr/local/jdk/

wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u141-b15/336fa29ff2bb4ef291e347e091f7f4a7/jdk-8u141-linux-x64.tar.gz"

#校验解压JDK的文件夹
if [ -e $jdkDir ]
then
	echo "$jdkDir文件夹已存在,覆盖执行!"
else
	mkdir -p $jdkDir
fi

echo "检查压缩包是否存在..."
	mkdir $jdkDir
	echo "开始解压..."
	tar -zxvf jdk-8u141-linux-x64.tar.gz -C $jdkDir
	for file in $jdkDir*
	do
		if [ -d $file ]
		then
			JAVA_HOME=$file
		fi
	done
	echo "JAVA_HOME=$JAVA_HOME"


#修改环境变量

echo "export JAVA_HOME=$JAVA_HOME" >> /etc/profile
echo "export PATH=\$PATH:\$JAVA_HOME/bin" >> /etc/profile
echo "export CLASSPATH=.:\$JAVA_HOME/lib/dt.jar:\$JAVA_HOME/lib/tools.jar" >> /etc/profile

echo "jdk 安装配置完成"
source /etc/profile
cd /workspace
rm -rf /workspace/aiCommand
git clone https://github.com/zaizhuzhu123/aiCommand.git
nohup /usr/local/jdk/jdk1.8.0_141/bin/java -jar /workspace/aiCommand/target/soft/aiCommand.jar 222 &
#/usr/local/jdk/jdk1.8.0_141/bin/java -jar aiCommand/target/soft/aiCommand.jar
echo "程序运行成功"
echo `java -version`
