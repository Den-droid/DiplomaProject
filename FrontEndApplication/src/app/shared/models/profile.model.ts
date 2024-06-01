import { ProfileField } from "./field.model";
import { Indices } from "./indices";
import { Page } from "./page.model";

export class ProfileByLabel {
  constructor(public name: string, public scientometricSystemName: string, public faculty: string, public chair: string) { }
}

export class ProfileForUser {
  constructor(public name: string, public indices: Indices, public recommendations: string[]) { }
}

export class ProfilePreview {
  constructor(public id: number, public scientistName: string,
    public areWorksDoubtful: boolean, public isActive: boolean) { }
}

export class GetProfilesDto {
  constructor(public profiles: ProfilePreview[], public pageDto: Page) { }
}


export class UpdateProfileDto {
  constructor(public fields: ProfileField[], public labelsIds: number[]) { }
}

export class CreateProfileDto {
  constructor(public scientistId: number, public scientometricSystemId: number,
    public profileFields: ProfileField[], public labelsIds: number[]) { }
}
