import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { LoadingComponent } from './loading/loading.component';
import { UploaderComponent } from './uploader/uploader.component';

@NgModule({
  declarations: [ LoadingComponent, UploaderComponent ],
  imports: [
    CommonModule
  ],
  exports: [ LoadingComponent, UploaderComponent ]
})
export class SharedModule { }
