import { Page } from "./page.model";

export class ProfilePreview {
  constructor(public id: number, public scientistName: string,
    public areWorksDoubtful: boolean, public isActive: boolean) { }
}

export class GetProfilesDto {
  constructor(public profiles: ProfilePreview[], public pageDto: Page) { }
}

