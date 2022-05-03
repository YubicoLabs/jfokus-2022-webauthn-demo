// Copyright 2022 Yubico AB
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package demo.webauthn;

import lombok.Value;
import org.mindrot.jbcrypt.BCrypt;

@Value
public class PasswordBcrypt {
  private static final int BCRYPT_WORK_FACTOR = 12;

  String bcrypt;

  public static PasswordBcrypt hash(final Secret<String> newPassword) {
    return new PasswordBcrypt(
        BCrypt.hashpw(newPassword.reveal(), BCrypt.gensalt(BCRYPT_WORK_FACTOR)));
  }
}
