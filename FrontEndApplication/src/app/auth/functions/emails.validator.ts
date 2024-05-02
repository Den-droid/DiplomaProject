export function ValidateEmails(email: string) : boolean{
  const emailRegex = /^[A-Za-z0-9._%-+]+@[A-Za-z0-9._%-+]+\.[a-z]{2,3}/;
  return email.match(emailRegex) != null;
}
