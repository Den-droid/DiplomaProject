import { RoleLabel, RoleName } from "../constants/roles.constant";

export class Role {
  constructor(public id: number, public name: string) { }
}

export class UpdateDefaultPermissions {
  constructor(public roleId: number, public defaultPermissionsIds: number[]) { }
}

export function mapStringToRoleName(name: string): RoleName {
  return RoleName[name as keyof typeof RoleName];
}

export function mapStringToRoleLabel(name: string): RoleLabel {
  return RoleLabel[name as keyof typeof RoleLabel];
}
