import { Page } from "./page.model";

export class User {
  constructor(public id: number, public email: string, public fullName: string,
    public isApproved: boolean, public isActive: boolean, public isSignedUp: boolean) { }
}

export class GetUsersDto {
  constructor(public users: User[], public pageDto: Page) {
  }
}

export class CreateAdminDto {
  constructor(public email: string, public facultyIds: number[], public chairIds: number[],
    public isMainAdmin: boolean, public permissions: number[]
  ) { }
}

export class UpdateAdminDto {
  constructor(public fullName: string, public facultyIds: number[], public chairIds: number[], public permissions: number[]
  ) { }
}

export class UpdateUserDto {
  constructor(public fullName: string, public permissionIds: number[]) { }
}

export class UpdateCurrentUserDto {
  constructor(public fullName: string) { }
}
