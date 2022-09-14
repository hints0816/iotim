### introduce 

web-im demo

![图片](https://user-images.githubusercontent.com/44020055/184276414-18a83bbd-4023-40a7-bd82-b7d82bd1361b.png)

![图片](https://user-images.githubusercontent.com/44020055/184276403-75cac86b-c8eb-45e5-8b7c-29cc120a669b.png)

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