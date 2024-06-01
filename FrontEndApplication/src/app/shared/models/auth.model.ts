export class SignInDto {
  constructor(public email: string, public password: string) {
  }
}

export class TokensDto {
  constructor(public accessToken: string, public refreshToken: string) {
  }
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

export class RefreshTokenDto {
  constructor(public refreshToken: string) { }
}
