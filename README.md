### introduce 

web-im demo

![图片](https://user-images.githubusercontent.com/44020055/190129526-f002a97e-1f38-4813-accf-73fd8d6ecb29.png)

- netty
- spring-web
- spring-security


### Object Storage
##### minio
###### minio server(window)
> set MINIO_ROOT_USER=admin
  set MINIO_ROOT_PASSWORD=12345678
  minio.exe server --console-address :9000 --address :9003 E:/Minio_Data

###### minio client(window)
> mc.exe config host add minio http://localhost:9003 minio 12345678
  mc policy set public minio/gscm/img
