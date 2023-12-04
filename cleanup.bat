D:
cd D:\openkm\tomcat-8.5.34\repository
net stop "tomcat (managed by AlwaysUpService)"
rmdir /s /q "index","cache"
net start "tomcat (managed by AlwaysUpService)"