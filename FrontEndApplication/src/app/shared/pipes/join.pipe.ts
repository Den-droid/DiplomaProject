import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'join'
})
export class JoinStringsPipe implements PipeTransform {
  transform(strings: string[]): string {
    let res = '';
    for (let string of strings) {
      res += string + ', ';
    }
    res = res.substring(0, res.length - 2);
    return res;
  }
}
