import { ScientometricSystemLabel, ScientometricSystemName } from "../constants/scientometric-system.constant";

export class ScientometricSystem {
  constructor(public id: number, public name: string, public nextMinImportDate: Date) {

  }
}

export class ExtractionErrors {
  constructor(public status: string, public scientistsNames: string[]) { }
}

export function mapStringToScientometricSystemName(name: string): ScientometricSystemName {
  return ScientometricSystemName[name as keyof typeof ScientometricSystemName];
}

export function mapStringToScientometricSystemLabel(name: string): ScientometricSystemLabel {
  return ScientometricSystemLabel[name as keyof typeof ScientometricSystemLabel];
}

