import { registerLocaleData } from '@angular/common';
import * as fr from '@angular/common/locales/fr';
import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app.component';
import { appConfig } from './app/app.config';

registerLocaleData(fr.default);

bootstrapApplication(App, appConfig).catch((err) => console.error(err));
