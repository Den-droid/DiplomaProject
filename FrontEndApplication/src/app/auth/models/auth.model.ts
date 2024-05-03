export enum Role {
  MAIN_ADMIN, FACULTY_ADMIN, CHAIR_ADMIN, USER
}

export class SignInDto {
  constructor(public email: string, public password: string) {
  }
}

export class RoleTokensDto {
  constructor(public role: Role, public accessToken: string, public refreshToken: string) {
  }
}

export class SignUpScientistDto {
  constructor(public id: number, public scientistName: string) { }
}

export class SignUpDto {
  constructor(public email: string, public password: string, public scientistId: number) { }
}

export class ForgotPasswordDto {
  constructor(public email: string) { }
}

export class ChangePasswordDto {
  constructor(public newPassword: string) { }
}

export class SignUpByInviteDto {
  constructor(public fullName: string, public password: string) { }
}
