export class EntityIndices {
  constructor(public name: string, public indices: Indices) {

  }
}

export class Indices {
  constructor(public citationIndex: number, public hirshIndex: number) { }
}
