import { Injectable } from '@angular/core';
import * as AWS from 'aws-sdk/global';
import S3 from 'aws-sdk/clients/s3';
import { s3Credentials } from '../../models/auth_config';

import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})

export class UploaderService {

  uploadCounter = 0;

  constructor(private alertService: ToastrService) {}

  uploadFile(file, totalFiles: number) {
    this.uploadCounter++;
    const contentType = file.type;
    const bucket = new S3({
      accessKeyId: s3Credentials.accessKeyId,
      secretAccessKey: s3Credentials.secretAccessKey,
      region: s3Credentials.region
    });
    const params = {
      Bucket: `planpackrepeat-new`,
      Key: file.name,
      Body: file,
      ACL: 'public-read',
      ContentType: contentType
    };
    bucket.upload(params, (err, data) => {
      if (err) {
        console.log('There was an error uploading your file: ', err);
        return false;
      }
      this.uploadCounter--;
      if (this.uploadCounter === 0) {
        this.alertService.success('Successfully uploaded the photo(s)!');
      }
      console.log('Successfully uploaded file.', data);
      return true;
    });

    // for upload progress
    bucket.upload(params).on('httpUploadProgress', (evt) => {
          console.log(evt.loaded + ' of ' + evt.total + ' Bytes');
      }).send((err, data) => {
          if (err) {
              console.log('There was an error uploading your file: ', err);
              return false;
          }
          console.log('Successfully uploaded file.', data);
          return true;
      });
  }

  resetUploadCounter() {
    this.uploadCounter = 0;
  }
}
