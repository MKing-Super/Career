模块端口设置：
eureka-server   9100
api-gateway     9200
work-service    9001
work-admin      9002
life-service    9003
life-admin      9004
home-service    9005
home-admin      9006

防火墙打开端口：
firewall-cmd --permanent --add-port=9100/tcp
firewall-cmd --permanent --add-port=9200/tcp
firewall-cmd --permanent --add-port=9001/tcp
firewall-cmd --permanent --add-port=9002/tcp
firewall-cmd --permanent --add-port=9003/tcp
firewall-cmd --permanent --add-port=9004/tcp
firewall-cmd --permanent --add-port=9005/tcp
firewall-cmd --permanent --add-port=9006/tcp
firewall-cmd --reload

