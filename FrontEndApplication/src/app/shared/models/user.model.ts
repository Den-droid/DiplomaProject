import { Page } from "./page.model";

export class User {
  constructor(public id: number, public email: string, public fullName: string,
    public isApproved: boolean, public isActive: boolean, public isSignedUp : boolean) { }
}

export class GetUsersDto {
  constructor(public users: User[], public pageDto: Page) {
  }
}

export class AddAdminDto {
  constructor(public email: string, public facultyIds: number[], public chairIds: number[],
    public isMainAdmin: boolean, public permissions: number[]
  ) { }
}

export class EditAdminDto {
  constructor(public fullName: string, public facultyIds: number[], public chairIds: number[], public permissions: number[]
  ) { }
}

export class EditUserDto {
  constructor(public fullName: string) { }
}
