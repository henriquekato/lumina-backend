# lumina backend

# tipos de usuários

- Aluno
- Professor
- Administrador

# endpoints

| tipo de método | endpoint                                                         | tipo de usuário         | descrição                                |
|----------------|------------------------------------------------------------------|-------------------------|------------------------------------------|
| POST           | /login                                                           | admin, professor, aluno | faz login e recebe token JWT             |
| GET            | /admin                                                           | admin                   | lista todos os admin                     |
| GET            | /admin/{id}                                                      | admin                   | retorna um admin                         |
| POST           | /admin                                                           | admin                   | cria novo admin                          |
| DELETE         | /admin/{id}                                                      | admin                   | deleta um admin                          |
| GET            | /professor                                                       | admin                   | lista todos os professores               |
| GET            | /professor/{id}                                                  | admin                   | retorna um professor                     |
| POST           | /professor                                                       | admin                   | cria novo professor                      |
| DELETE         | /professor/{id}                                                  | admin                   | deleta um professor                      |
| GET            | /student                                                         | admin                   | lista todos os aluno                     |
| GET            | /student/{id}                                                    | admin                   | retorna um aluno                         |
| POST           | /student                                                         | admin                   | cria novo aluno                          |
| DELETE         | /student/{id}                                                    | admin                   | deleta um aluno                          |
| GET            | /classroom                                                       | admin                   | lista todos as salas                     |
| GET            | /classroom/{id}                                                  | admin, professor, aluno | retorna um sala                          |
| POST           | /classroom                                                       | admin                   | cria uma sala                            |
| DELETE         | /classroom/{id}                                                  | admin                   | deleta uma sala                          |
| POST           | /classroom/{classroomId}/students/{studentId}                    | admin, professor        | cadastra um aluno em uma sala            |
| DELETE         | /classroom/{classroomId}/students/{studentId}                    | admin, professor        | remove um aluno de uma sala              |
| GET            | /classroom/{classroomId}/task                                    | admin, professor, aluno | lista todas as atividades de uma sala    |
| GET            | /classroom/{classroomId}/task/{taksId}                           | admin, professor, aluno | retorna uma atividade de uma sala        |
| POST           | /classroom/{classroomId}/task                                    | admin, professor        | cria uma atividade para uma sala         |
| DELETE         | /classroom/{classroomId}/task/{taksId}                           | admin, professor        | deleta uma atividade de uma sala         |
| GET            | /classroom/{classroomId}/task/{taskId}/submission                | admin, professor        | lista todas as entregas de uma atividade |
| GET            | /classroom/{classroomId}/task/{taskId}/submission/{submissionId} | admin, professor, aluno | retorna uma entrega de uma atividade     |
| POST           | /classroom/{classroomId}/task/{taskId}/submission                | aluno                   | entrega uma atividade                    |
| DELETE         | /classroom/{classroomId}/task/{taskId}/submission/{submissionId} | aluno                   | deleta a entrega de uma atividade        |
