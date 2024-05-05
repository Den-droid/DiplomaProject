import { ScientometricSystemName } from "../constants/scientometric-system.constant";

export class ScientometricSystem {
  constructor(public id: number, public name: string, public nextMinImportDate: Date) {

  }
}

export function mapStringToScientometricSystem(name: string): ScientometricSystemName {
  return ScientometricSystemName[name as keyof typeof ScientometricSystemName];
}
