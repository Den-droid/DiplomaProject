import { HttpRequest } from "@angular/common/http";

export function isApiRequest(req: HttpRequest<any>) {
  return req.url.indexOf('/api/') > -1;
}
