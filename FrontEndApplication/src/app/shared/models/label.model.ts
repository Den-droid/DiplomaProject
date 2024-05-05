import { Page } from "./page.model";

export class Label {
  constructor(public id: number, public name: string) {

  }
}

export class GetLabelsDto {
  constructor(public labels: Label[], public pageDto: Page) {

  }
}

export class AddLabelDto {
  constructor(public name: string) {

  }
}

export class EditLabelDto {
  constructor(public name: string) {

  }
}

export class DeleteLabelDto {
  constructor(public replacementLabelId: number) {

  }
}
